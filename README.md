# PBox #

PBox is a client/server solution to replicate a filesystem across a network, think Dropbox but private, running on your servers.
Currently all packets are transmitted over TCP in CLEARTEXT - eventually I will add encryption.

This is really basic and buggy right now, it should stabilise soon though!

Protocol format detailed in comments in each Message type (see pbox-core).

## TODO ##

### Client ###

  * Implement MOVED/RENAMED (deleted and added with same hash).
    * Add protocol support for this too?
  * Handle protocol acks.
  * Toggle to ignore hidden files/directories (.DS_Store)
  * Delete MessageQueue.java
  * Use ClientState?

### Server ###

  * Zero length payload signifies directory. There's a bug when server treats empty file as directory.
  * User ServerState
  * Handle/send acks correctly.

### Future Plans ###

  * Delete directories that are not empty.
  * Handle large files; io.netty.handler.codec.TooLongFrameException: Adjusted frame length exceeds 104857.
  * Test across different operating systems (Linux/MacOS/Windows).
  * Add end to end encryption.
    * Implement Diffie Hellman or SRP handshakes for key exchange.
  * Streaming.
  * Rewrite Client/Server such that they are same and can be used as either/both.
  * Implement Server in different language for fun?!