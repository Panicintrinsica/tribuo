/*
 * Copyright (c) 2024. Emrys Corbin (https://corbin.dev/). All Rights Reserved.
 */

package msu.itc475.tribuo.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import msu.itc475.tribuo.types.Fan;

public interface FanRepository extends JpaRepository<Fan, Long> {
}