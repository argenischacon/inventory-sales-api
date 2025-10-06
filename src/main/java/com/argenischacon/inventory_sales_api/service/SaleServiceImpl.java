package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.SaleDetailRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;
import com.argenischacon.inventory_sales_api.exception.InsufficientStockException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.SaleMapper;
import com.argenischacon.inventory_sales_api.model.Customer;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.model.Sale;
import com.argenischacon.inventory_sales_api.model.SaleDetail;
import com.argenischacon.inventory_sales_api.repository.CustomerRepository;
import com.argenischacon.inventory_sales_api.repository.ProductRepository;
import com.argenischacon.inventory_sales_api.repository.SaleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private final CustomerRepository customerRepository;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public SaleResponseDTO create(SaleRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + dto.getCustomerId() + " not found."));

        Sale sale = new Sale();
        sale.setDate(LocalDate.now());
        sale.setCustomer(customer);
        List<SaleDetail> saleDetails = mapSaleDetailFromDto(dto.getSaleDetails(), sale);
        sale.getSaleDetails().addAll(saleDetails);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponseDTO update(Long id, SaleRequestDTO dto) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " not found."));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer with id " + dto.getCustomerId() + " not found."));

        sale.setCustomer(customer);

        //Map of current SaleDetail
        Map<Long, SaleDetail> existingSaleDetails = sale.getSaleDetails()
                .stream()
                .collect(Collectors.toMap(SaleDetail::getId, Function.identity()));

        Set<Long> incomingDetailIds = dto.getSaleDetails().stream()
                .map(SaleDetailRequestDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingSaleDetails.values().stream()
                .filter(detail -> !incomingDetailIds.contains(detail.getId()))
                .forEach(detailToRemove -> {
                    //Restore stock by adding the quantity back
                    updateStock(detailToRemove.getProduct(), detailToRemove.getQuantity());
                });


        List<SaleDetail> saleDetails = mapSaleDetailFromDto(dto.getSaleDetails(), sale, existingSaleDetails);

        sale.getSaleDetails().clear();
        sale.getSaleDetails().addAll(saleDetails);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " not found."));

        //Restore stock for each product in the sale
        sale.getSaleDetails().forEach(detail -> updateStock(detail.getProduct(), detail.getQuantity()));

        saleRepository.deleteById(id);
    }

    @Override
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " not found."));

        return saleMapper.toResponse(sale);
    }

    @Override
    public List<SaleResponseDTO> findAll() {
        return saleMapper.toResponseList(saleRepository.findAll());
    }

    private void updateStock(Product product, Integer quantityChange) {
        int newStock = product.getStock() + quantityChange;
        if (newStock < 0) {
            int requested = -quantityChange;
            int available = product.getStock();
            String message = String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d.",
                    product.getName(), requested, available);
            throw new InsufficientStockException(message, product.getId(), requested, available);
        }
        product.setStock(newStock);
    }

    private List<SaleDetail> mapSaleDetailFromDto(List<SaleDetailRequestDTO> detailRequestDTOS, Sale sale) {
        return mapSaleDetailFromDto(detailRequestDTOS, sale, null);
    }

    private List<SaleDetail> mapSaleDetailFromDto(List<SaleDetailRequestDTO> detailRequestDTOS, Sale sale, Map<Long, SaleDetail> existingDetailsMap) {
        List<Long> productIds = detailRequestDTOS.stream()
                .map(SaleDetailRequestDTO::getProductId)
                .toList();

        Map<Long, Product> productsMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return detailRequestDTOS.stream()
                .map(dto -> {
                    Product product = productsMap.get(dto.getProductId());
                    if (product == null) {
                        throw new ResourceNotFoundException("Product with id " + dto.getProductId() + " not found.");
                    }

                    SaleDetail saleDetail;
                    int quantityChange = dto.getQuantity();

                    if (dto.getId() != null) {
                        //verify if it belongs to the sale
                        if (existingDetailsMap == null || !existingDetailsMap.containsKey(dto.getId())) {
                            throw new ResourceNotFoundException("In this sale with id " + sale.getId() + ", there is no sale detail with id " + dto.getId() + ".");
                        }
                        // update detail
                        saleDetail = existingDetailsMap.get(dto.getId());
                        quantityChange = dto.getQuantity() - saleDetail.getQuantity();
                    } else {
                        // create new detail
                        saleDetail = new SaleDetail();
                        saleDetail.setSale(sale);
                    }

                    // Check and update stock
                    updateStock(product, -quantityChange); // Negative  to decrease stock
                    saleDetail.setQuantity(dto.getQuantity());
                    saleDetail.setUnitPrice(dto.getUnitPrice());
                    saleDetail.setProduct(product);
                    return saleDetail;
                })
                .toList();
    }
}
