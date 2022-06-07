import axios from 'axios';

class ApiService {

    upload(file , config) {

        return axios.post("http://localhost:8080/excel/uploadFile/convert-to-single-json", file, config);
    }

    download(fileName, config) {
      return axios.get("http://localhost:8080/downloadFile/" + fileName, config);
    }
}

export default new ApiService();