import axios from "axios";

const CSRF_TOKEN = document.cookie.match(new RegExp(`XSRF-TOKEN=([^;]+)`))[1];


export default axios.create({
  baseURL: "http://localhost:8080",
  headers: [{
    "Content-type": "multipart/form-data"
  }, { "X-XSRF-TOKEN": CSRF_TOKEN }]
});