package com.wallet.crypto.trustapp.ui.widget.holder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wallet.crypto.trustapp.R;
import com.wallet.crypto.trustapp.entity.Token;
import com.wallet.crypto.trustapp.entity.TokenTicker;
import com.wallet.crypto.trustapp.ui.widget.OnTokenClickListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TokenHolder extends BinderViewHolder<Token> implements View.OnClickListener {

    public static final int VIEW_TYPE = 1005;
    private static final String EMPTY_BALANCE = "\u2014\u2014";

    private final TextView symbol;
    private final TextView balanceEth;
    private final TextView balanceCurrency;
    private final ImageView icon;

    private Token token;
    private OnTokenClickListener onTokenClickListener;

    public TokenHolder(int resId, ViewGroup parent) {
        super(resId, parent);

        icon = findViewById(R.id.icon);
        symbol = findViewById(R.id.symbol);
        balanceEth = findViewById(R.id.balance_eth);
        balanceCurrency = findViewById(R.id.balance_currency);
        itemView.setOnClickListener(this);
    }

    @Override
    public void bind(@Nullable Token data, @NonNull Bundle addition) {
        this.token = data;
        try {
            // We handled NPE. Exception handling is expensive, but not impotent here
            symbol.setText(TextUtils.isEmpty(token.tokenInfo.name)
                        ? token.tokenInfo.symbol
                        : getString(R.string.token_name, token.tokenInfo.name, token.tokenInfo.symbol));

            BigDecimal decimalDivisor = new BigDecimal(Math.pow(10, token.tokenInfo.decimals));
            BigDecimal ethBalance = token.tokenInfo.decimals > 0
                    ? token.balance.divide(decimalDivisor) : token.balance;
            ethBalance = ethBalance.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros();
            String value = ethBalance.compareTo(BigDecimal.ZERO) == 0 ? "0" : ethBalance.toPlainString();
            this.balanceEth.setText(value);
            TokenTicker ticker = token.ticker;
            if (ticker == null) {
                this.balanceCurrency.setText(EMPTY_BALANCE);
                fillIcon(null, R.mipmap.token_logo);
            } else {
                fillCurrency(ethBalance, ticker);
                fillIcon(ticker.image, R.mipmap.token_logo);
            }
        } catch (Exception ex) {
            fillEmpty();
        }
    }

    private void fillIcon(String imageUrl, int defaultResId) {
        if (TextUtils.isEmpty(imageUrl)) {
            icon.setImageResource(defaultResId);
        } else {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .fit()
                    .centerInside()
                    .placeholder(defaultResId)
                    .error(defaultResId)
                    .into(icon);
        }
    }

    private void fillCurrency(BigDecimal ethBalance, TokenTicker ticker) {
        String converted = ethBalance.compareTo(BigDecimal.ZERO) == 0
                ? EMPTY_BALANCE
                : ethBalance.multiply(new BigDecimal(ticker.price))
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        String formattedPercents = "";
        int color = Color.RED;
        try {
            double percentage = Double.valueOf(ticker.percentChange24h);
            color = ContextCompat.getColor(getContext(), percentage < 0 ? R.color.red : R.color.green);
            formattedPercents = "(" + (percentage < 0 ? "" : "+") + ticker.percentChange24h + "%)";
        } catch (Exception ex) { /* Quietly */ }
        String lbl = getString(R.string.token_balance,
                ethBalance.compareTo(BigDecimal.ZERO) == 0 ? "" : "$",
                converted, formattedPercents);
        Spannable spannable = new SpannableString(lbl);
        spannable.setSpan(new ForegroundColorSpan(color),
                converted.length() + 1, lbl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        this.balanceCurrency.setText(spannable);
    }

    private void fillEmpty() {
        balanceEth.setText(R.string.NA);
        balanceCurrency.setText(EMPTY_BALANCE);
    }

    @Override
    public void onClick(View v) {
        if (onTokenClickListener != null) {
            onTokenClickListener.onTokenClick(v, token);
        }
    }

    public void setOnTokenClickListener(OnTokenClickListener onTokenClickListener) {
        this.onTokenClickListener = onTokenClickListener;
    }
}
