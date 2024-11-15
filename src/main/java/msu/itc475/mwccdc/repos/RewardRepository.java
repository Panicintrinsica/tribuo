/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.mwccdc.repos;

import msu.itc475.mwccdc.dto.RewardResponse;
import msu.itc475.mwccdc.types.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    void deleteAll();

    @Query("SELECT new msu.itc475.mwccdc.dto.RewardResponse(f.firstName, f.lastName, f.email, f.phone, f.preferredStand, r.seatId) " +
            "FROM Fan f INNER JOIN f.reward r ORDER BY f.preferredStand, f.reward.seatId")

    List<RewardResponse> findRewardsWithFanDetails();
}