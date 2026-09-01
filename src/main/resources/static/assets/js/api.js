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
            } catch {}

            const error = new Error(body?.detail ?? `HTTP error ${response.status}`);
            error.status = response.status;
            error.body = body;
            throw error;
        }

        if (response.status === 204) {
            return null;
        }

        const contentType = response.headers.get("content-type") ?? "";
        return contentType.includes("application/json")
            ? response.json()
            : response.text();
    }
};
