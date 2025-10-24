package indi.yoloz.sample.mybatis.service;

import indi.yoloz.sample.mybatis.entity.Prpibnrmodel;

public interface PrpibnrmodelMapper {

    /**
     *
     * description:deleteByPrimaryKey
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int deleteByPrimaryKey(String taskcode);

    /**
     *
     * description:insert
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int insert(Prpibnrmodel record);

    /**
     *
     * description:insertSelective
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int insertSelective(Prpibnrmodel record);


    /**
     *
     * description:selectByPrimaryKey
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    Prpibnrmodel selectByPrimaryKey(String taskcode);


    /**
     *
     * description:updateByPrimaryKeySelective
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int updateByPrimaryKeySelective(Prpibnrmodel record);

    /**
     *
     * description:updateByPrimaryKeyWithBLOBs
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int updateByPrimaryKeyWithBLOBs(Prpibnrmodel record);

    /**
     *
     * description:updateByPrimaryKey
     *
     * @author: yunhua
     * @date: 2018-10-08
     * @since: 1.0
     */
    int updateByPrimaryKey(Prpibnrmodel record);
}