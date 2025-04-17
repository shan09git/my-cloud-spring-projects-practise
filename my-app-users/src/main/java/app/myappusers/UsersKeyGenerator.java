package app.myappusers;

import java.util.UUID;

class UsersKeyGenerator {

    static String generateUUIDKey() {
        return UUID.randomUUID().toString();
    }

}
