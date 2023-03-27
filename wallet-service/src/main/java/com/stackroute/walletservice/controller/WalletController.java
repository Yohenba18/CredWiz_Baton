package com.stackroute.walletservice.controller;

import com.stackroute.walletservice.entity.SendMoneyRequest;
import com.stackroute.walletservice.entity.Transaction;
import com.stackroute.walletservice.entity.WalletRequest;
import com.stackroute.walletservice.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.stackroute.walletservice.entity.Wallet;
import com.stackroute.walletservice.service.WalletService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @PostMapping("/createWallet")
    public ResponseEntity<?> createWallet(@Valid @RequestBody Wallet wallet, BindingResult result) throws WalletAlreadyExistsException {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<Object>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            Long contactNumber = wallet.getContactNumber();
            Wallet existingWallet = walletService.getWalletByContactNumber(contactNumber);
            if (existingWallet != null) {
                throw new WalletAlreadyExistsException("Wallet already exists for contact number: " + contactNumber);
            }
            walletService.addWallet(wallet);
            return new ResponseEntity<String>("Wallet created successfully", HttpStatus.CREATED);
        } catch (WalletAlreadyExistsException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/getWallet/{contactNumber}")
    public ResponseEntity<?> getWalletByContactNumber(@PathVariable("contactNumber") long id)
            throws WalletNotExistsException {
        try {
            Wallet wallet = walletService.getWalletByContactNumber(id);
            if (wallet == null) {
                throw new WalletNotExistsException("No wallet found for contact number: " + id);
            }
            return new ResponseEntity<Wallet>(wallet, HttpStatus.OK);
        } catch (WalletNotExistsException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addmoney")
    public ResponseEntity<String> addMoneyToWallet(@RequestBody WalletRequest request)
            throws WalletNotExistsException {
        try {
            Long ContactNumber = request.getContactNumber();
            Double amount = request.getAmount();
            Wallet wallet = walletService.getWalletByContactNumber(ContactNumber);
            if (wallet == null) {
                throw new WalletNotExistsException("No wallet found for phone number: " + ContactNumber);
            }
            wallet.setBalance(wallet.getBalance() + amount);
            walletService.addWallet(wallet);
            Long transactionAmount = request.getAmount().longValue();
            walletService.saveTransaction(request.getContactNumber(),
                    "Credit", "Successful", "Added money to wallet", null, transactionAmount);

            return new ResponseEntity<String>("Amount added successfully", HttpStatus.OK);
        } catch (WalletNotExistsException e) {
            walletService.saveTransaction(request.getContactNumber(),
                    "Credit", "Failed", "No wallet found for phone number", null, request.getAmount().longValue());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/withdrawmoney")
    public ResponseEntity<String> withdrawMoney(@RequestBody WalletRequest request)
            throws WalletNotExistsException, InSufficientBalanceException {
        try {
            Wallet wallet = walletService.getWalletByContactNumber(request.getContactNumber());
            if (wallet == null) {
                throw new WalletNotExistsException("No wallet found for phone number: " + request.getContactNumber());
            } else if (wallet.getBalance() < request.getAmount()) {
                throw new InSufficientBalanceException("Insufficient balance in wallet with phone number: " + request.getContactNumber());
            }
            wallet.setBalance(wallet.getBalance() - request.getAmount());
            walletService.addWallet(wallet);
            Long transactionAmount = request.getAmount().longValue();
            walletService.saveTransaction(request.getContactNumber(), "Withdraw Money",
                    "Successful", "Withdrawn money from wallet", null, transactionAmount);

            return new ResponseEntity<String>("Amount withdrawn successfully", HttpStatus.OK);
        } catch (WalletNotExistsException e) {
            walletService.saveTransaction(request.getContactNumber(),
                    "Debit", "Failed", "No wallet found for phone number", null, request.getAmount().longValue());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (InSufficientBalanceException e) {
            walletService.saveTransaction(request.getContactNumber(),
                    "Debit", "Failed", "Insufficient balance in wallet", null, request.getAmount().longValue());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sendmoney")
    public ResponseEntity<String> sendMoney(@RequestBody SendMoneyRequest sendMoneyRequest)
            throws InSufficientBalanceException, WalletNotExistsException {
        try {
            Long senderContactNumber = sendMoneyRequest.getSenderContactNumber();
            Long receiverContactNumber = sendMoneyRequest.getReceiverContactNumber();

            Wallet senderWallet = walletService.getWalletByContactNumber(senderContactNumber);
            if (senderWallet == null) {
                throw new WalletNotExistsException("Sender wallet not found with phone number: " + senderContactNumber);
            }

            Wallet receiverWallet = walletService.getWalletByContactNumber(receiverContactNumber);
            if (receiverWallet == null) {
                throw new WalletNotExistsException("Receiver wallet not found with phone number: " + receiverContactNumber);
            }
            boolean isTransactionSuccessful = walletService.sendMoney(sendMoneyRequest.getSenderContactNumber(),
                    sendMoneyRequest.getReceiverContactNumber(), sendMoneyRequest.getAmount());

            String senderMessage, receiverMessage;
            if (isTransactionSuccessful) {
                senderMessage = "Sent money to receiver";
                receiverMessage = "Received money from sender";
            } else {
                senderMessage = "Insufficient balance in wallet";
                receiverMessage = "Transaction failed due to sender's insufficient balance";
            }


            walletService.saveTransaction(sendMoneyRequest.getSenderContactNumber(), "Debit",
                    isTransactionSuccessful ? "Successful" : "Failed", senderMessage, String.valueOf(receiverWallet.getContactNumber()), sendMoneyRequest.getAmount().longValue());
            walletService.saveTransaction(sendMoneyRequest.getReceiverContactNumber(), "Credit",
                    isTransactionSuccessful ? "Successful" : "Failed", receiverMessage, String.valueOf(senderWallet.getContactNumber()), sendMoneyRequest.getAmount().longValue());

            if (isTransactionSuccessful) {
                return new ResponseEntity<String>("Transaction successful", HttpStatus.OK);
            } else {
                throw new InSufficientBalanceException("Insufficient balance in sender's wallet with phone number: " + sendMoneyRequest.getSenderContactNumber());
            }
        } catch (InSufficientBalanceException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (WalletNotExistsException e) {
            walletService.saveTransaction(sendMoneyRequest.getSenderContactNumber(),
                    "Debit", "Failed", "Sender wallet not found", null, sendMoneyRequest.getAmount().longValue());
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/transactionHistory/{contactNumber}")
    public ResponseEntity<?> getTransactionHistory(@PathVariable("contactNumber") long contactNumber) {
        Wallet wallet = walletService.getWalletByContactNumber(contactNumber);
        if (wallet == null) {
            return new ResponseEntity<String>("Wallet not found for contact number: " + contactNumber, HttpStatus.NOT_FOUND);
        }
        List<Transaction> transactions = wallet.getTransactions();
        return new ResponseEntity<List<Transaction>>(transactions, HttpStatus.OK);
    }
}