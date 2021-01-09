package config

class Config {

    static baseUrl = ['fixer':'http://localhost:8080/fixer']
    static apiKeys = ['fixer': 'dUYejUupPl39gip5f1wzTjdsLHHOGoOV']

    static getBaseUrl(app) {
        return baseUrl[app];
    }
    
    static getApiKey(app) {
        return apiKeys[app];
    }
}
