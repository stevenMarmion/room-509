const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

async function get_api(url) {
    const response = await fetch(`${API_BASE_URL}${url}`, {
        credentials: 'include',
    });
    if (!response.ok) {
        throw new Error(`GET ${url} failed: ${response.status}`);
    }
    return response.json();
}

async function post_api(url, body) {
    const response = await fetch(`${API_BASE_URL}${url}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(body),
    });
    if (!response.ok) {
        throw new Error(`POST ${url} failed: ${response.status}`);
    }
    if (response.status === 204) {
        return null;
    }
    return response.json();
}

async function put_api(url, body) {
    const response = await fetch(`${API_BASE_URL}${url}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: 'include',
        body: JSON.stringify(body),
    });
    if (!response.ok) {
        throw new Error(`PUT ${url} failed: ${response.status}`);
    }
    return response.json();
}

async function delete_api(url) {
    const response = await fetch(`${API_BASE_URL}${url}`, {
        method: "DELETE",
    });
    if (!response.ok) {
        throw new Error(`DELETE ${url} failed: ${response.status}`);
    }
    return response.ok;
}

export { get_api, post_api, put_api, delete_api };
