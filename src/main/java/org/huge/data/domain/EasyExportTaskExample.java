package org.huge.data.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EasyExportTaskExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public EasyExportTaskExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodIsNull() {
            addCriterion("db_function_method is null");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodIsNotNull() {
            addCriterion("db_function_method is not null");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodEqualTo(String value) {
            addCriterion("db_function_method =", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodNotEqualTo(String value) {
            addCriterion("db_function_method <>", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodGreaterThan(String value) {
            addCriterion("db_function_method >", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodGreaterThanOrEqualTo(String value) {
            addCriterion("db_function_method >=", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodLessThan(String value) {
            addCriterion("db_function_method <", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodLessThanOrEqualTo(String value) {
            addCriterion("db_function_method <=", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodLike(String value) {
            addCriterion("db_function_method like", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodNotLike(String value) {
            addCriterion("db_function_method not like", value, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodIn(List<String> values) {
            addCriterion("db_function_method in", values, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodNotIn(List<String> values) {
            addCriterion("db_function_method not in", values, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodBetween(String value1, String value2) {
            addCriterion("db_function_method between", value1, value2, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionMethodNotBetween(String value1, String value2) {
            addCriterion("db_function_method not between", value1, value2, "dbFunctionMethod");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsIsNull() {
            addCriterion("db_function_params is null");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsIsNotNull() {
            addCriterion("db_function_params is not null");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsEqualTo(String value) {
            addCriterion("db_function_params =", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsNotEqualTo(String value) {
            addCriterion("db_function_params <>", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsGreaterThan(String value) {
            addCriterion("db_function_params >", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsGreaterThanOrEqualTo(String value) {
            addCriterion("db_function_params >=", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsLessThan(String value) {
            addCriterion("db_function_params <", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsLessThanOrEqualTo(String value) {
            addCriterion("db_function_params <=", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsLike(String value) {
            addCriterion("db_function_params like", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsNotLike(String value) {
            addCriterion("db_function_params not like", value, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsIn(List<String> values) {
            addCriterion("db_function_params in", values, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsNotIn(List<String> values) {
            addCriterion("db_function_params not in", values, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsBetween(String value1, String value2) {
            addCriterion("db_function_params between", value1, value2, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbFunctionParamsNotBetween(String value1, String value2) {
            addCriterion("db_function_params not between", value1, value2, "dbFunctionParams");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessIsNull() {
            addCriterion("db_results_process is null");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessIsNotNull() {
            addCriterion("db_results_process is not null");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessEqualTo(String value) {
            addCriterion("db_results_process =", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessNotEqualTo(String value) {
            addCriterion("db_results_process <>", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessGreaterThan(String value) {
            addCriterion("db_results_process >", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessGreaterThanOrEqualTo(String value) {
            addCriterion("db_results_process >=", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessLessThan(String value) {
            addCriterion("db_results_process <", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessLessThanOrEqualTo(String value) {
            addCriterion("db_results_process <=", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessLike(String value) {
            addCriterion("db_results_process like", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessNotLike(String value) {
            addCriterion("db_results_process not like", value, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessIn(List<String> values) {
            addCriterion("db_results_process in", values, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessNotIn(List<String> values) {
            addCriterion("db_results_process not in", values, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessBetween(String value1, String value2) {
            addCriterion("db_results_process between", value1, value2, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andDbResultsProcessNotBetween(String value1, String value2) {
            addCriterion("db_results_process not between", value1, value2, "dbResultsProcess");
            return (Criteria) this;
        }

        public Criteria andFileNameIsNull() {
            addCriterion("file_name is null");
            return (Criteria) this;
        }

        public Criteria andFileNameIsNotNull() {
            addCriterion("file_name is not null");
            return (Criteria) this;
        }

        public Criteria andFileNameEqualTo(String value) {
            addCriterion("file_name =", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameNotEqualTo(String value) {
            addCriterion("file_name <>", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameGreaterThan(String value) {
            addCriterion("file_name >", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameGreaterThanOrEqualTo(String value) {
            addCriterion("file_name >=", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameLessThan(String value) {
            addCriterion("file_name <", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameLessThanOrEqualTo(String value) {
            addCriterion("file_name <=", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameLike(String value) {
            addCriterion("file_name like", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameNotLike(String value) {
            addCriterion("file_name not like", value, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameIn(List<String> values) {
            addCriterion("file_name in", values, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameNotIn(List<String> values) {
            addCriterion("file_name not in", values, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameBetween(String value1, String value2) {
            addCriterion("file_name between", value1, value2, "fileName");
            return (Criteria) this;
        }

        public Criteria andFileNameNotBetween(String value1, String value2) {
            addCriterion("file_name not between", value1, value2, "fileName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameIsNull() {
            addCriterion("download_url_processor_class_name is null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameIsNotNull() {
            addCriterion("download_url_processor_class_name is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameEqualTo(String value) {
            addCriterion("download_url_processor_class_name =", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameNotEqualTo(String value) {
            addCriterion("download_url_processor_class_name <>", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameGreaterThan(String value) {
            addCriterion("download_url_processor_class_name >", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameGreaterThanOrEqualTo(String value) {
            addCriterion("download_url_processor_class_name >=", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameLessThan(String value) {
            addCriterion("download_url_processor_class_name <", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameLessThanOrEqualTo(String value) {
            addCriterion("download_url_processor_class_name <=", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameLike(String value) {
            addCriterion("download_url_processor_class_name like", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameNotLike(String value) {
            addCriterion("download_url_processor_class_name not like", value, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameIn(List<String> values) {
            addCriterion("download_url_processor_class_name in", values, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameNotIn(List<String> values) {
            addCriterion("download_url_processor_class_name not in", values, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameBetween(String value1, String value2) {
            addCriterion("download_url_processor_class_name between", value1, value2, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorClassNameNotBetween(String value1, String value2) {
            addCriterion("download_url_processor_class_name not between", value1, value2, "downloadUrlProcessorClassName");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsIsNull() {
            addCriterion("download_url_processor_params is null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsIsNotNull() {
            addCriterion("download_url_processor_params is not null");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsEqualTo(String value) {
            addCriterion("download_url_processor_params =", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsNotEqualTo(String value) {
            addCriterion("download_url_processor_params <>", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsGreaterThan(String value) {
            addCriterion("download_url_processor_params >", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsGreaterThanOrEqualTo(String value) {
            addCriterion("download_url_processor_params >=", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsLessThan(String value) {
            addCriterion("download_url_processor_params <", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsLessThanOrEqualTo(String value) {
            addCriterion("download_url_processor_params <=", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsLike(String value) {
            addCriterion("download_url_processor_params like", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsNotLike(String value) {
            addCriterion("download_url_processor_params not like", value, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsIn(List<String> values) {
            addCriterion("download_url_processor_params in", values, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsNotIn(List<String> values) {
            addCriterion("download_url_processor_params not in", values, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsBetween(String value1, String value2) {
            addCriterion("download_url_processor_params between", value1, value2, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andDownloadUrlProcessorParamsNotBetween(String value1, String value2) {
            addCriterion("download_url_processor_params not between", value1, value2, "downloadUrlProcessorParams");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeIsNull() {
            addCriterion("trigger_executed_time is null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeIsNotNull() {
            addCriterion("trigger_executed_time is not null");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeEqualTo(Date value) {
            addCriterion("trigger_executed_time =", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeNotEqualTo(Date value) {
            addCriterion("trigger_executed_time <>", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeGreaterThan(Date value) {
            addCriterion("trigger_executed_time >", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("trigger_executed_time >=", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeLessThan(Date value) {
            addCriterion("trigger_executed_time <", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeLessThanOrEqualTo(Date value) {
            addCriterion("trigger_executed_time <=", value, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeIn(List<Date> values) {
            addCriterion("trigger_executed_time in", values, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeNotIn(List<Date> values) {
            addCriterion("trigger_executed_time not in", values, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeBetween(Date value1, Date value2) {
            addCriterion("trigger_executed_time between", value1, value2, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andTriggerExecutedTimeNotBetween(Date value1, Date value2) {
            addCriterion("trigger_executed_time not between", value1, value2, "triggerExecutedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeIsNull() {
            addCriterion("finished_time is null");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeIsNotNull() {
            addCriterion("finished_time is not null");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeEqualTo(Date value) {
            addCriterion("finished_time =", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeNotEqualTo(Date value) {
            addCriterion("finished_time <>", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeGreaterThan(Date value) {
            addCriterion("finished_time >", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("finished_time >=", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeLessThan(Date value) {
            addCriterion("finished_time <", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeLessThanOrEqualTo(Date value) {
            addCriterion("finished_time <=", value, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeIn(List<Date> values) {
            addCriterion("finished_time in", values, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeNotIn(List<Date> values) {
            addCriterion("finished_time not in", values, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeBetween(Date value1, Date value2) {
            addCriterion("finished_time between", value1, value2, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andFinishedTimeNotBetween(Date value1, Date value2) {
            addCriterion("finished_time not between", value1, value2, "finishedTime");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameIsNull() {
            addCriterion("upload_class_name is null");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameIsNotNull() {
            addCriterion("upload_class_name is not null");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameEqualTo(String value) {
            addCriterion("upload_class_name =", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameNotEqualTo(String value) {
            addCriterion("upload_class_name <>", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameGreaterThan(String value) {
            addCriterion("upload_class_name >", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameGreaterThanOrEqualTo(String value) {
            addCriterion("upload_class_name >=", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameLessThan(String value) {
            addCriterion("upload_class_name <", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameLessThanOrEqualTo(String value) {
            addCriterion("upload_class_name <=", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameLike(String value) {
            addCriterion("upload_class_name like", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameNotLike(String value) {
            addCriterion("upload_class_name not like", value, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameIn(List<String> values) {
            addCriterion("upload_class_name in", values, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameNotIn(List<String> values) {
            addCriterion("upload_class_name not in", values, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameBetween(String value1, String value2) {
            addCriterion("upload_class_name between", value1, value2, "uploadClassName");
            return (Criteria) this;
        }

        public Criteria andUploadClassNameNotBetween(String value1, String value2) {
            addCriterion("upload_class_name not between", value1, value2, "uploadClassName");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}