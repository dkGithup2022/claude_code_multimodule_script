package io.multi.hello.model.user;

import lombok.Value;
import java.time.Instant;

@Value
public class User implements UserModel {
    Long userId;
    String email;
    String name;
    Instant createdAt;
    Instant updatedAt;
}