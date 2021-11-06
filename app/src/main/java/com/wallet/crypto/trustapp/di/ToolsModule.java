package com.wallet.crypto.trustapp.di;

import android.content.Context;

import com.google.gson.Gson;
import com.wallet.crypto.trustapp.App;
import com.wallet.crypto.trustapp.repository.PasswordStore;
import com.wallet.crypto.trustapp.repository.TrustPasswordStore;
import com.wallet.crypto.trustapp.service.RealmManager;
import com.wallet.crypto.trustapp.util.LogInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
class ToolsModule {
	@Provides
	Context provideContext(App application) {
		return application.getApplicationContext();
	}

	@Singleton
	@Provides
	Gson provideGson() {
		return new Gson();
	}

	@Singleton
	@Provides
	OkHttpClient okHttpClient() {
		return new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .connectTimeout(15, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)
                .build();
	}

	@Singleton
	@Provides
	PasswordStore passwordStore(Context context) {
		return new TrustPasswordStore(context);
	}

	@Singleton
    @Provides
    RealmManager provideRealmManager() {
	    return new RealmManager();
    }
}
