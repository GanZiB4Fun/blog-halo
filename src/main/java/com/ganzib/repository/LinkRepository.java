package com.ganzib.repository;

import com.ganzib.model.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 *     友情链接持久层
 * </pre>
 *
 * @author : RYAN0UP
 * @date : 2017/11/14
 */
public interface LinkRepository extends JpaRepository<Link, Long> {
}
