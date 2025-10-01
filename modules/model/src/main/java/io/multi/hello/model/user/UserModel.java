package io.multi.hello.model.user;

import io.multi.hello.model.AuditProps;

public interface UserModel extends AuditProps {
    Long getUserId();
    String getEmail();
    String getName();
}