package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.SaleDetailRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{
    private final CustomerRepository customerRepository;
    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public SaleResponseDTO create(SaleRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Sale sale = new Sale();
        sale.setDate(LocalDate.now());
        sale.setCustomer(customer);
        List<SaleDetail> saleDetails = mapSaleDetail(dto, sale);
        sale.getSaleDetails().addAll(saleDetails);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional
    public SaleResponseDTO update(Long id, SaleRequestDTO dto) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        sale.setCustomer(customer);

        //Map of current SaleDetail
        Map<Long, SaleDetail> existingSaleDetails = sale.getSaleDetails()
                .stream()
                .collect(Collectors.toMap(SaleDetail::getId, saleDetail -> saleDetail));

        List<SaleDetail> updateDetails = new ArrayList<>();

        for (SaleDetailRequestDTO requestDTO : dto.getSaleDetails()) {
            if (requestDTO.getId() != null && existingSaleDetails.containsKey(requestDTO.getId())) {
                // already exists -> update
                Product foundProduct = findProductById(requestDTO.getProductId());

                SaleDetail saleDetail = existingSaleDetails.get(requestDTO.getId());
                saleDetail.setQuantity(requestDTO.getQuantity());
                saleDetail.setUnitPrice(requestDTO.getUnitPrice());
                saleDetail.setProduct(foundProduct);

                updateDetails.add(saleDetail);
            } else {
                // does not exist -> add
                Product foundProduct = findProductById(requestDTO.getProductId());

                SaleDetail newSaleDetail = new SaleDetail();
                newSaleDetail.setQuantity(requestDTO.getQuantity());
                newSaleDetail.setUnitPrice(requestDTO.getUnitPrice());
                newSaleDetail.setProduct(foundProduct);
                newSaleDetail.setSale(sale);

                updateDetails.add(newSaleDetail);
            }
        }

        // replace list with updated details
        sale.getSaleDetails().clear();
        sale.getSaleDetails().addAll(updateDetails);

        return saleMapper.toResponse(saleRepository.save(sale));
    }

    @Override
    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sale not found");
        }
        saleRepository.deleteById(id);
    }

    @Override
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));

        return saleMapper.toResponse(sale);
    }

    @Override
    public List<SaleResponseDTO> findAll() {
        return saleMapper.toResponseList(saleRepository.findAll());
    }

    private List<SaleDetail> mapSaleDetail(SaleRequestDTO dto, Sale sale) throws RuntimeException {
        return dto.getSaleDetails().stream()
                .map(requestDTO -> {
                    Product product = findProductById(requestDTO.getProductId());
                    SaleDetail saleDetail = new SaleDetail();
                    saleDetail.setQuantity(requestDTO.getQuantity());
                    saleDetail.setUnitPrice(requestDTO.getUnitPrice());
                    saleDetail.setProduct(product);
                    saleDetail.setSale(sale);
                    return saleDetail;
                }).toList();
    }

    private Product findProductById(Long id) throws RuntimeException{
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
