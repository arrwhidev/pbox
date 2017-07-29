# Fresh Startup

  * Connect to server.
  * Create index.
    * Hash = hex(hash(filename + data)) 
  * Write a TransportFileMessage to server for each indexed file.
    * MetaData.to = index item path relative to root index directory.
    * Flags.isDirectory = is the index item a directory.
    * Payload = index item byte[] or empty byte[] for directory.
  * Write index file to disk.
  * Start monitoring disk for changes using FileSystemWatcher.
    * IndexUpdater observes; to update the index.
    * MessageWriter observes; to write changes to server.
    


