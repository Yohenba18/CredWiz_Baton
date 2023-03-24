package com.stackroute.walletservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.stackroute.walletservice.entity.Wallet;
import com.stackroute.walletservice.repository.WalletRepository;

@SpringBootTest
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @MockBean
    private WalletRepository walletRepository;

    private Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        wallet.setPhoneNumber(1234567890L);
        wallet.setAadhaarNumber(123456789012L);
        wallet.setPanNumber("ABCDE1234F");
        wallet.setBankName("SBI");
        wallet.setAccountNumber(123456789012L);
        wallet.setAmount(500.0);
    }

    @Test
    public void testAddWallet() {
        when(walletRepository.save(wallet)).thenReturn(wallet);
        Wallet addedWallet = walletService.addWallet(wallet);
        assertEquals(wallet, addedWallet);
    }

    @Test
    public void testGetWalletByPhoneNumber() {
        when(walletRepository.findById(wallet.getPhoneNumber())).thenReturn(Optional.of(wallet));
        Wallet foundWallet = walletService.getWalletByPhoneNumber(wallet.getPhoneNumber());
        assertEquals(wallet, foundWallet);
    }
}