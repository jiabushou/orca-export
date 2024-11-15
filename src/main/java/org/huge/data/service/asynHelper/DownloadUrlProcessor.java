package org.huge.data.service.asynHelper;

import java.util.Map;

public interface DownloadUrlProcessor {

    void manageDownUrl(String downloadUrl, Map<String, String> manageDownloadUrlParams);
}
