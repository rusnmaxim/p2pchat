
async function logout() {
    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            credentials: 'include'
        });

        localStorage.clear();
        sessionStorage.clear();

        const idToken = await response.text();

        const logoutUrl = 'https://mrusnac2.ngrok.app/realms/chatrealm/protocol/openid-connect/logout'
                        + '?client_id=chat-client'
                         + `&id_token_hint=${idToken}`
                        + '&post_logout_redirect_uri=https://mrusnac.ngrok.app/';

        window.location.href = logoutUrl;

    } catch (error) {
        const fallbackLogoutUrl = 'https://mrusnac2.ngrok.app/realms/chatrealm/protocol/openid-connect/logout'
                                + '?client_id=chat-client';
        window.location.href = fallbackLogoutUrl;
    }
}
