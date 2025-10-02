package io.example.coupon.infrastructure.coupon.repository.jdbc;

import io.example.coupon.infrastructure.coupon.repository.CouponRepository;
import io.example.coupon.model.coupon.Coupon;
import io.example.coupon.model.coupon.CouponIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CouponJdbcRepository implements CouponRepository {

    private final CouponEntityRepository entityRepository;

    @Override
    public Optional<Coupon> findById(CouponIdentity identity) {
        return entityRepository.findById(identity.getCouponId())
                .map(this::toModel);
    }

    @Override
    public List<Coupon> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Coupon> findByName(String name) {
        return entityRepository.findByName(name).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = toEntity(coupon);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        CouponEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(CouponIdentity identity) {
        entityRepository.deleteById(identity.getCouponId());
    }

    @Override
    public boolean tryIncreaseIssuedCount(long couponId) {
        var affected = entityRepository.tryIncreaseIssuedCount(couponId);
        return affected == 1;
    }

    private Coupon toModel(CouponEntity entity) {
        return new Coupon(
                entity.getCouponId(),
                entity.getName(),
                entity.getDiscountAmount(),
                entity.getTotalQuantity(),
                entity.getIssuedCount(),
                entity.getUserId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CouponEntity toEntity(Coupon model) {
        return new CouponEntity(
                model.getCouponId(),
                model.getName(),
                model.getDiscountAmount(),
                model.getTotalQuantity(),
                model.getIssuedCount(),
                model.getUserId(),
                model.getStartDate(),
                model.getEndDate(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
