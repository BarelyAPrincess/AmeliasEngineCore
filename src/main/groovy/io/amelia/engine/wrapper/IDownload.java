package io.amelia.engine.wrapper;

import java.net.URI;
import java.nio.file.Path;

public interface IDownload
{
	void download( URI address, Path destination ) throws Exception;
}