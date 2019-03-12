package cube;

import com.qiniu.pandora.common.Constants;
import com.qiniu.pandora.common.PandoraClient;
import com.qiniu.pandora.common.PandoraClientImpl;
import com.qiniu.pandora.common.QiniuException;
import com.qiniu.pandora.http.Client;
import com.qiniu.pandora.http.Response;
import com.qiniu.pandora.util.Auth;
import com.qiniu.pandora.util.StringMap;

public class CubeClient {
    private PandoraClient pandoraClient;
    private String cubeHost;

    public CubeClient(PandoraClient pandoraClient) {
        this(pandoraClient, Constants.PIPELINE_HOST);
    }

    public CubeClient(PandoraClient pandoraClient, String logdbHost) {
        this.pandoraClient = pandoraClient;
        this.cubeHost = logdbHost;
    }

    /**
     * CubeClient工厂方法，使用官方Cube Host
     *
     * @param accessKey 七牛accessKey
     * @param secretKey 七牛secretKey
     * @return LogDBClient
     */
    public static CubeClient newCubeClient(String accessKey, String secretKey) {
        Auth auth = Auth.create(accessKey, secretKey);
        PandoraClientImpl pandoraClient = new PandoraClientImpl(auth);
        return new CubeClient(pandoraClient);
    }

    /**
     * CubeClient工厂方法，可自定义Cube Host
     *
     * @param accessKey 七牛accessKey
     * @param secretKey 七牛secretKey
     * @param logdbHost 自定义logdb logdbHost
     * @return LogDBClient
     */
    public static CubeClient newCubeClient(String accessKey, String secretKey, String logdbHost) {
        Auth auth = Auth.create(accessKey, secretKey);
        PandoraClientImpl pandoraClient = new PandoraClientImpl(auth);
        return new CubeClient(pandoraClient, logdbHost);
    }

    public byte[] query(String q) throws QiniuException {
        String putUrl = String.format("%s/v2/stream/cubes/query", this.cubeHost);
        Response r = this.pandoraClient.post(putUrl, q.getBytes(Constants.UTF_8), new StringMap(), Client.JsonMime);
        if (r.isOK()) {
            return r.body();
        } else {
            throw new QiniuException(r.bodyString());
        }
    }

}
