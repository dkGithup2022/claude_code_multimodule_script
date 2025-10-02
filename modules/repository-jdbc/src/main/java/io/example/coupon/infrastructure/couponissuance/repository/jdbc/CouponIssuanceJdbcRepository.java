package io.example.coupon.infrastructure.couponissuance.repository.jdbc;

import io.example.coupon.infrastructure.couponissuance.repository.CouponIssuanceRepository;
import io.example.coupon.model.couponissuance.CouponIssuance;
import io.example.coupon.model.couponissuance.CouponIssuanceIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CouponIssuanceJdbcRepository implements CouponIssuanceRepository {

    private final CouponIssuanceEntityRepository entityRepository;

    @Override
    public Optional<CouponIssuance> findById(CouponIssuanceIdentity identity) {
        return entityRepository.findById(identity.getIssuanceId())
                .map(this::toModel);
    }

    @Override
    public List<CouponIssuance> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponIssuance> findByCouponId(Long couponId) {
        return entityRepository.findByCouponId(couponId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponIssuance> findByUserId(Long userId) {
        return entityRepository.findByUserId(userId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponIssuance> findByCouponIdAndUserId(Long couponId, Long userId) {
        return entityRepository.findByCouponIdAndUserId(couponId, userId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public CouponIssuance save(CouponIssuance couponIssuance) {
        CouponIssuanceEntity entity = toEntity(couponIssuance);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        CouponIssuanceEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(CouponIssuanceIdentity identity) {
        entityRepository.deleteById(identity.getIssuanceId());
    }

    private CouponIssuance toModel(CouponIssuanceEntity entity) {
        return new CouponIssuance(
                entity.getIssuanceId(),
                entity.getCouponId(),
                entity.getUserId(),
                entity.getIssuedAt(),
                entity.getUsedAt(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CouponIssuanceEntity toEntity(CouponIssuance model) {
        return new CouponIssuanceEntity(
                model.getIssuanceId(),
                model.getCouponId(),
                model.getUserId(),
                model.getIssuedAt(),
                model.getUsedAt(),
                model.getStatus(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
