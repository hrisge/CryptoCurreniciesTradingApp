package com.cryptotrading.service.impl;

import com.cryptotrading.dto.ApplicationUserDTO;
import com.cryptotrading.dto.ApplicationUserHoldingDTO;
import com.cryptotrading.dto.TransactionDTO;
import com.cryptotrading.entity.ApplicationUser;
import com.cryptotrading.entity.ApplicationUserHolding;
import com.cryptotrading.entity.Transaction;
import com.cryptotrading.mapper.ApplicationUserHoldingMapper;
import com.cryptotrading.mapper.ApplicationUserMapper;
import com.cryptotrading.mapper.TransactionMapper;
import com.cryptotrading.repository.ApplicationUserHoldingRepository;
import com.cryptotrading.repository.ApplicationUserRepository;
import com.cryptotrading.repository.TransactionRepository;
import com.cryptotrading.service.IApplicationUserService;
import com.cryptotrading.service.IKrakenSocketService;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ApplicationUserServiceImpl implements IApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserMapper applicationUserMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ApplicationUserHoldingRepository applicationUserHoldingRepository;
    private final IKrakenSocketService krakenSocketService;
    private final ApplicationUserHoldingMapper applicationUserHoldingMapper;



    public ApplicationUserServiceImpl(ApplicationUserRepository applicationUserRepository, ApplicationUserMapper applicationUserMapper, TransactionRepository transactionRepository, TransactionMapper transactionMapper, ApplicationUserHoldingRepository applicationUserHoldingRepository, IKrakenSocketService krakenSocketService, ApplicationUserHoldingMapper applicationUserHoldingMapper) {
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserMapper = applicationUserMapper;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.applicationUserHoldingRepository = applicationUserHoldingRepository;
        this.krakenSocketService = krakenSocketService;
        this.applicationUserHoldingMapper = applicationUserHoldingMapper;
    }


    @Override
    public TransactionDTO buy(ApplicationUserDTO applicationUserDTO, String symbol, BigDecimal quantity) {
        if(applicationUserDTO == null || symbol == null || quantity == null){
            throw new NullPointerException("ApplicationUserDTO or symbol or quantity is NULL");
        }

        Long userId = applicationUserDTO.id();

        Optional<ApplicationUser> optionalUser = applicationUserRepository.findById(userId);

        if(optionalUser.isEmpty()){
            throw new IllegalArgumentException("User not found!");
        }

        ApplicationUser user= optionalUser.get();

        BigDecimal price = BigDecimal.valueOf((double) krakenSocketService.getDataByName(symbol).ask());
        BigDecimal totalCost = quantity.multiply(price);

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(totalCost));
        applicationUserRepository.save(user);

        Optional<ApplicationUserHolding> holdingOpt = applicationUserHoldingRepository.findByUserIdAndSymbol(userId, symbol);

        if(holdingOpt.isPresent()){
            ApplicationUserHolding holding = holdingOpt.get();
            holding.setQuantity(holding.getQuantity().add(quantity));

            applicationUserHoldingRepository.save(holding);
        } else{
            ApplicationUserHolding newHolding = new ApplicationUserHolding();
            newHolding.setUser(user);
            newHolding.setQuantity(quantity);
            newHolding.setSymbol(symbol);

            applicationUserHoldingRepository.save(newHolding);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setQuantity(quantity);
        transaction.setBought(true);
        transaction.setPrice(price);
        transaction.setTime(LocalDateTime.now());
        transaction.setTicker(symbol);

        transactionRepository.save(transaction);

        return transactionMapper.entityToDto(transaction);
    }

    @Override
    public TransactionDTO sell(ApplicationUserDTO applicationUserDTO, String symbol, BigDecimal quantity) {
        if (applicationUserDTO == null || symbol == null || quantity == null) {
            throw new NullPointerException("ApplicationUserDTO or symbol or quantity is NULL");
        }

        Long userId = applicationUserDTO.id();
        Optional<ApplicationUser> optionalUser = applicationUserRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found!");
        }

        ApplicationUser user = optionalUser.get();

        BigDecimal price = BigDecimal.valueOf((double) krakenSocketService.getDataByName(symbol).bid());
        BigDecimal totalRevenue = quantity.multiply(price);

        Optional<ApplicationUserHolding> holdingOpt = applicationUserHoldingRepository.findByUserIdAndSymbol(userId, symbol);

        if (holdingOpt.isEmpty() || holdingOpt.get().getQuantity().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient holdings to sell");
        }

        ApplicationUserHolding holding = holdingOpt.get();

        BigDecimal updatedQuantity = holding.getQuantity().subtract(quantity);
        if (updatedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            applicationUserHoldingRepository.delete(holding);
        } else {
            holding.setQuantity(updatedQuantity);
            applicationUserHoldingRepository.save(holding);
        }

        user.setBalance(user.getBalance().add(totalRevenue));
        applicationUserRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setTicker(symbol);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setTime(LocalDateTime.now());
        transaction.setBought(false);

        transactionRepository.save(transaction);

        return transactionMapper.entityToDto(transaction);
    }

    @Override
    public List<TransactionDTO> getPastTransactions(final Long userId) {
        if(userId == null){
            throw new NullPointerException("ApplicationUserDTO is NULL");
        }

        List<Transaction> transactions = transactionRepository.findAllByUser_Id(userId);

        return transactionMapper.entityListToDtoList(transactions);
    }

    @Override
    public ApplicationUserDTO logIn(ApplicationUserDTO applicationUserDTO) {
        if(applicationUserDTO == null){
            throw new NullPointerException("ApplicationUserDTO is NULL");
        }

        String username = applicationUserDTO.username();
        String password = applicationUserDTO.password();

        Optional<ApplicationUser> optionalApplicationUser = applicationUserRepository.findByUsername(username);

        if(optionalApplicationUser.isEmpty()){
            throw new IllegalArgumentException("Username not found!");
        }

        ApplicationUser user = optionalApplicationUser.get();

        if(!user.getPassword().equals(password)){
            throw new IllegalArgumentException("Password and username do not match!");
        }

        return applicationUserMapper.entityToDto(user);
    }

    @Override
    public ApplicationUserDTO register(ApplicationUserDTO applicationUserDTO) {
        if(applicationUserDTO == null) {
            throw new NullPointerException("ApplicationUserDTO is NULL");
        }

        Optional<ApplicationUser> optionalApplicationUser = applicationUserRepository.findByUsername(applicationUserDTO.username());

        if(!optionalApplicationUser.isEmpty()){
            throw new IllegalArgumentException("Username not found!");
        }

        ApplicationUser toRegister = new ApplicationUser();

        toRegister.setUsername(applicationUserDTO.username());
        toRegister.setEmail(applicationUserDTO.email());
        toRegister.setBalance(new BigDecimal("10000.00"));
        toRegister.setPassword(applicationUserDTO.password());

        applicationUserRepository.save(toRegister);

        return applicationUserMapper.entityToDto(toRegister);
    }

    @Override
    @Transactional
    public ApplicationUserDTO restart(ApplicationUserDTO applicationUserDTO) {
        if(applicationUserDTO == null) {
            throw new NullPointerException("ApplicationUserDTO is NULL");
        }

        Long userId = applicationUserDTO.id();

        Optional<ApplicationUser> optionalUser = applicationUserRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Username not found!");
        }

        ApplicationUser user = optionalUser.get();

        transactionRepository.deleteAllByUser_Id(userId);
        applicationUserHoldingRepository.deleteAllByUser_Id(userId);

        user.setBalance(new BigDecimal("10000.00"));
        applicationUserRepository.save(user);

        return applicationUserMapper.entityToDto(user);
    }

    @Override
    public List<ApplicationUserHoldingDTO> getPortfolio(Long id) {
        if(id == null){
            throw new NullPointerException("ApplicationUserDTO is NULL");
        }

        List<ApplicationUserHolding> optionalHoldings = applicationUserHoldingRepository.findByUserId(id);
        if(optionalHoldings.isEmpty()){
            throw new IllegalArgumentException("No such holdings");
        }

        return applicationUserHoldingMapper.entityListToDtoList(optionalHoldings);
    }
}
