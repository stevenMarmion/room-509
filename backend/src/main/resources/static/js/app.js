import { get_api, post_api, put_api, delete_api } from "./service/api.js";

const secret_config = await get_api('/api/config');

console.log(`Secret config value: ${secret_config}`);