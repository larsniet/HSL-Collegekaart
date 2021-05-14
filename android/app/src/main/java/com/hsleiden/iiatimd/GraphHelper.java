package com.hsleiden.iiatimd;

import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

// Singleton class - the app only needs a single instance
// of the Graph client
public class GraphHelper implements IAuthenticationProvider {
    private static GraphHelper INSTANCE = null;
    private GraphServiceClient mClient = null;

    private GraphHelper() {
        AuthenticationHelper authProvider = AuthenticationHelper.getInstance();

        mClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider).buildClient();
    }

    public static synchronized GraphHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GraphHelper();
        }

        return INSTANCE;
    }

    public CompletableFuture<User> getUser() {
        // GET /me (logged in user)
        return mClient.me().buildRequest()
                .select("displayName,mail,mailboxSettings,userPrincipalName,employeeId,birthday")
                .getAsync();
    }

    @Nonnull
    @Override
    public CompletableFuture<String> getAuthorizationTokenAsync(@NotNull URL requestUrl) {
        return null;
    }
}