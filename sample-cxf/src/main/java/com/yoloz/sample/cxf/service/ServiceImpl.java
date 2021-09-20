package com.yoloz.sample.cxf.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yoloz.sample.cxf.dao.enitity.AlarmInfo;
import com.yoloz.sample.cxf.dao.enitity.AuditResponse;
import com.yoloz.sample.cxf.dao.enitity.AuditResult;
import com.yoloz.sample.cxf.dao.enitity.FlowInfo;
import com.yoloz.sample.cxf.dao.enitity.HourAudit;
import com.yoloz.sample.cxf.dao.enitity.NorResponse;
import com.yoloz.sample.cxf.dao.enitity.ServiceDelInfo;
import com.yoloz.sample.cxf.dao.enitity.ServiceInfo;
import com.yoloz.sample.cxf.dao.enitity.ServiceStatus;
import com.yoloz.sample.cxf.dao.enitity.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ServiceImpl implements IService {

    private final Logger logger = LoggerFactory.getLogger(ServiceImpl.class);

    @Override
    public NorResponse addService(ServiceInfo serviceInfo) {
        return justTest();
    }

    @Override
    public NorResponse modService(ServiceInfo serviceInfo) {
        return justTest();
    }

    @Override
    public NorResponse delService(ServiceDelInfo serviceDelInfo) {
        return justTest();
    }

    @Override
    public NorResponse updateServiceStatus(ServiceStatus serviceStatus) {
        return justTest();
    }

    @Override
    public AuditResponse hourAudit(HourAudit hourAudit) {
        AuditResponse auditResponse = new AuditResponse();
        auditResponse.setIdentification("GXPT");
        auditResponse.setVersion("1.0");
        List<AuditResult> auditResults = new ArrayList<>();
        AuditResult auditResult = new AuditResult();
        try {
            auditResponse.setIp(InetAddress.getLocalHost().getHostAddress());
            auditResult.setResult("ok");
        } catch (UnknownHostException e) {
            auditResponse.setIp("127.0.0.1");
            auditResult.setResult("error");
            auditResult.setDesc("20001");
        }
        auditResults.add(auditResult);
        auditResponse.setAuditResults(auditResults);
        return auditResponse;
    }

    @Override
    public NorResponse serviceFlowAuditInfo(FlowInfo flowInfo) {
        return justTest();
    }

    @Override
    public NorResponse serviceAlarmAuditInfo(AlarmInfo alarmInfo) {
        return justTest();
    }

    @Override
    public NorResponse reportTest(TestInfo testInfo) {
        logger.info(new Gson().toJson(testInfo, new TypeToken<TestInfo>() {
        }.getType()));
        return justTest();
    }

    private NorResponse justTest() {
        NorResponse norResponse = new NorResponse();
        norResponse.setIdentification("GXPT");
        norResponse.setVersion("1.0");
        try {
            norResponse.setIp(InetAddress.getLocalHost().getHostAddress());
            norResponse.setResult("ok");
        } catch (UnknownHostException e) {
            norResponse.setResult("error");
            norResponse.setIp("127.0.0.1");
            norResponse.setDesc(e.getMessage());
        }
        return norResponse;
    }
}
