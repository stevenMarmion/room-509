async function get_api(url) {
    const response = await fetch(url);
    if (!response.ok) {
        throw new Error(`GET ${url} failed: ${response.status}`);
    }
    return response.json();
}

async function post_api(url, body) {
    const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
    });
    if (!response.ok) {
        throw new Error(`POST ${url} failed: ${response.status}`);
    }
    return response.json();
}

async function put_api(url, body) {
    const response = await fetch(url, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
    });
    if (!response.ok) {
        throw new Error(`PUT ${url} failed: ${response.status}`);
    }
    return response.json();
}

async function delete_api(url) {
    const response = await fetch(url, {
        method: "DELETE",
    });
    if (!response.ok) {
        throw new Error(`DELETE ${url} failed: ${response.status}`);
    }
    return response.ok;
}

export { get_api, post_api, put_api, delete_api };
