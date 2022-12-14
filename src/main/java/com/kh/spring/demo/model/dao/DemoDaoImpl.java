package com.kh.spring.demo.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.demo.model.dto.Dev;

@Repository
public class DemoDaoImpl implements DemoDao {

//	sqlSession을 의존주입받아 사용.
	@Autowired
	private SqlSessionTemplate sqlSession;

	@Override
	public int insertDev(Dev dev) {
		return sqlSession.insert("demo.insertDev", dev);
	}

	@Override
	public List<Dev> selectDevList() {
		return sqlSession.selectList("demo.selectDevList");
	}

	@Override
	public Dev selectOneDev(int no) {
		return sqlSession.selectOne("demo.selectOneDev", no);
	}

	@Override
	public int updateDev(Dev dev) {
		return sqlSession.update("demo.updateDev", dev);
	}

	@Override
	public int deleteDev(int no) {
		return sqlSession.delete("demo.deleteDev", no);
	}
	
	@Override
	public int updatePartialDev(Dev dev) {
		return sqlSession.update("demo.updatePartialDev", dev);
	}
}
