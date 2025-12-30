package org.koppe.homeplanner.homeplanner_api.jpa.repository;

import org.koppe.homeplanner.homeplanner_api.jpa.entitiy.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

}
