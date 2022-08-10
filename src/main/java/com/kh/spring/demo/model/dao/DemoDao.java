package com.kh.spring.demo.model.dao;

import java.util.List;

import com.kh.spring.demo.model.dto.Dev;

public interface DemoDao {

	int insertDev(Dev dev);

	List<Dev> selectDevList();

	Dev selectOneDev(int no);

}
