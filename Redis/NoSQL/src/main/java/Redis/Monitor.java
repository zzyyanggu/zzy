package Redis;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import java.io.File;

public class Monitor {
    FileAlterationMonitor monitor;
    public Monitor(long interval) {
        monitor = new FileAlterationMonitor(interval);
    }

    //Add listener
    public void addMonitor(String path, FileAlterationListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(new File(path));
        monitor.addObserver(observer);
        observer.addListener(listener);
    }

    public void Start() throws Exception {
        monitor.start();
    }
}
