package org.daaya.daayalearningapp.exo.ui.video;

import androidx.annotation.NonNull;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSink;
import androidx.media3.datasource.DataSpec;
import androidx.media3.datasource.cache.Cache;
import androidx.media3.datasource.cache.CacheDataSink;
import androidx.media3.test.utils.FailOnCloseDataSink;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link DataSink} that can simulate caching the bytes being written to it based on a flag
 */
@UnstableApi
public final class CustomDataSink implements DataSink {

  /** Factory to create a {@link FailOnCloseDataSink}. */
  public static final class Factory implements DataSink.Factory {

    private final Cache cache;
    private final int fragmentSize;
    private final AtomicBoolean dontWrite;

    /**
     * Creates an instance.
     *
     * @param cache The cache to write to when not in dontWrite mode.
     * @param dontWrite An {@link AtomicBoolean} whose value is read in each call to {@link #open}
     *     to determine whether to enable the read that's being started.
     */
    public Factory(Cache cache, int fragmentSize, AtomicBoolean dontWrite) {
      this.cache = cache;
      this.dontWrite = dontWrite;
      this.fragmentSize = fragmentSize;
    }

    @NonNull
    @Override
    public DataSink createDataSink() {
      return new CustomDataSink(cache, fragmentSize, dontWrite);
    }
  }

  private final CacheDataSink wrappedSink;
  private final AtomicBoolean dontWrite;
  private boolean currentReadFailOnClose;

  /**
   * Creates an instance.
   *
   * @param cache The cache to write to when not in dontWrite mode.
   * @param dontWrite An {@link AtomicBoolean} whose value is read in each call to {@link #open}
   *     to determine whether to enable dontWrite the read that's being started.
   */
  public CustomDataSink(Cache cache, int fragmentSize, AtomicBoolean dontWrite) {
    this.wrappedSink = new CacheDataSink(cache, fragmentSize);
    this.dontWrite = dontWrite;
  }

  @Override
  public void open(@NonNull DataSpec dataSpec) throws IOException {
    currentReadFailOnClose = dontWrite.get();
    if (currentReadFailOnClose) {
      return;
    }
    wrappedSink.open(dataSpec);
  }

  @Override
  public void write(@NonNull byte[] buffer, int offset, int length) throws IOException {
    if (currentReadFailOnClose) {
      return;
    }
    wrappedSink.write(buffer, offset, length);
  }

  @Override
  public void close() throws IOException {
    wrappedSink.close();
  }
}
