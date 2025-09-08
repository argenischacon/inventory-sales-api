package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.ProductNestedDTO;
import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.mapper.SaleDetailMapper;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.model.SaleDetail;
import com.argenischacon.inventory_sales_api.repository.SaleDetailRepository;
import com.argenischacon.inventory_sales_api.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleDetailServiceTest {
    @Mock
    private SaleDetailRepository saleDetailRepository;

    @Mock
    private SaleDetailMapper saleDetailMapper;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SaleDetailServiceImpl saleDetailService;

    private SaleDetail baseSaleDetail;
    private SaleDetailResponseDTO baseSaleDetailResponseDTO;
    private Product baseProduct;
    private ProductNestedDTO baseProductNestedDTO;

    @BeforeEach
    void setUp(){
        baseProduct = new Product();
        baseProduct.setId(1L);
        baseProduct.setName("Smart TV");

        baseProductNestedDTO = new ProductNestedDTO();
        baseProductNestedDTO.setId(1L);
        baseProductNestedDTO.setName("Smart TV");

        baseSaleDetail = new SaleDetail();
        baseSaleDetail.setId(1L);
        baseSaleDetail.setProduct(baseProduct);

        baseSaleDetailResponseDTO = new SaleDetailResponseDTO();
        baseSaleDetailResponseDTO.setId(1L);
        baseSaleDetailResponseDTO.setProduct(baseProductNestedDTO);
    }

    @Test
    void shouldFindSaleDetailsBySaleIdWhenIdExists(){
        Long saleId = 1L;

        when(saleRepository.existsById(saleId)).thenReturn(true);
        when(saleDetailRepository.findBySaleId(saleId)).thenReturn(List.of(baseSaleDetail));
        when(saleDetailMapper.toResponseList(List.of(baseSaleDetail))).thenReturn(List.of(baseSaleDetailResponseDTO));

        List<SaleDetailResponseDTO> result = saleDetailService.findBySaleId(1L);

        assertEquals(1, result.size());
        assertEquals("Smart TV", result.getFirst().getProduct().getName());

        verify(saleRepository).existsById(saleId);
        verify(saleDetailRepository).findBySaleId(saleId);
        verify(saleDetailMapper).toResponseList(List.of(baseSaleDetail));
    }

    @Test
    void shouldThrowExceptionWhenSaleDoesNotExist(){
        Long saleId = 99L;

        when(saleRepository.existsById(saleId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> saleDetailService.findBySaleId(saleId));

        assertEquals("Sale not found", ex.getMessage());
        verify(saleRepository).existsById(saleId);
        verifyNoInteractions(saleDetailRepository, saleDetailMapper);
    }
}
