package io.example.coupon.model.user;

import io.example.coupon.model.AuditProps;

public interface UserModel extends AuditProps {
    Long getUserId();
    String getEmail();
    String getName();
}
