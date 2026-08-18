window.api = {

    async request(url, options = {}) {

        const response = await fetch(url, {
            credentials: "same-origin",
            ...options
        });

        if (!response.ok) {
            let body = null;

            try {
                body = await response.json();
            } catch (_) {
                // response может не содержать JSON
            }

            const error = new Error(
                body?.detail ?? `HTTP error ${response.status}`
            );

            error.status = response.status;
            error.body = body;

            throw error;
        }

        if (response.status === 204) {
            return null;
        }

        const contentType =
            response.headers.get("content-type") ?? "";

        if (contentType.includes("application/json")) {
            return response.json();
        }

        return response.text();
    }
};