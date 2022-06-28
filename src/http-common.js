import axios from 'axios';

class ApiService {

    upload(file , config) {

        return axios.post("http://localhost:8080/excel/uploadFile/convert-to-single-json", file, config);
    }

    uploadToMultipleJSON(file , config) {

      return axios.post("http://localhost:8080/excel/uploadFile/convert-to-multiple-json", file, config);
  }

    download(fileName, config) {
      return axios.get("http://localhost:8080/downloadFile/" + fileName, config);
    }

    downloadRule(fileName, config){
      return axios.get("http://localhost:8080/downloadRule/" + fileName, config);
    }

    countRules(){
      return axios.get("http://localhost:8080/rules/countRules");
    }

    createRule(rule, ruleName){
      return axios.post("http://localhost:8080/rules/createRule", rule, ruleName);
    }

    downloadMasterSpreadsheet(config){
      return axios.get("http://localhost:8080/rules/download-rule-spreadsheet" , config);
    }
}

export default new ApiService();