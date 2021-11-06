package com.wallet.crypto.trustapp.repository;

import com.wallet.crypto.trustapp.entity.Token;
import com.wallet.crypto.trustapp.entity.Wallet;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface TokenRepositoryType {

    Observable<Token[]> fetchActive(String walletAddress);

    Observable<Token[]> fetchAll(String walletAddress);

    Completable addToken(Wallet wallet, String address, String symbol, int decimals);

    Completable setEnable(Wallet wallet, Token token, boolean isEnabled);
}
