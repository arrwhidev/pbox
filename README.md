# PBox #

PBox is a client/server solution to replicate a filesystem across a network, think Dropbox but private, running on your servers.
Currently all packets are transmitted over TCP in CLEARTEXT - eventually I will add encryption.

This is really basic and buggy right now, it should stabilise soon though!

Protocol format detailed in comments in each Message type (see pbox-core).

## TODO ##

### Client ###

  * Send Index differences to Server on startup.
  * Only write to Index if something has changed on startup.
  * Usage of MessageWriter between FileSystemIndexer & FileSystemWatcher is hacky - make nicer!
  * Implement MOVED/RENAMED (deleted and added with same hash).
    * Add protocol support for this too?
  * Handle protocol acks.
  * Toggle to ignore hidden files/directories (.DS_Store)
  * Delete MessageQueue.java
  * Use ClientState?

### Server ###

  * User ServerState
  * Handle/send acks correctly.

### Future Plans ###

  * Handle large files; io.netty.handler.codec.TooLongFrameException: Adjusted frame length exceeds 104857.
  * Test across different operating systems (Linux/MacOS/Windows).
  * Add end to end encryption.
    * Implement Diffie Hellman or SRP handshakes for key exchange.
  * Streaming.
  * Rewrite Client/Server such that they are same and can be used as either/both.
  * Implement Server in different language for fun?!