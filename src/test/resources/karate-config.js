function _() {
    var env = karate.env; // get java system property 'karate.env'

    karate.log('karate.env selected environment was:', env);

    if (!env) {
        env = 'dev'; //env can be anything: dev, qa, staging, etc.
        karate.configure("ssl", false);
    }

    var config = {
        env: env
    };

    karate.configure('readTimeout', 10000);

    if(env == 'dev') {
        config.API_HOST= 'http://localhost:8085';
        karate.configure('readTimeout', 120000);
    }

    karate.configure('connectTimeout', 10000);

    return config;
}