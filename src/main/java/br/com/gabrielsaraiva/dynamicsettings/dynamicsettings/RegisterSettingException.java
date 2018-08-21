package br.com.gabrielsaraiva.dynamicsettings.dynamicsettings;

class RegisterSettingException extends RuntimeException {

    RegisterSettingException(Throwable e) {
        super(e);
    }

    RegisterSettingException(String msg) {
        super(msg);
    }

}

