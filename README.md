URL website = new URL("http://www.website.com/information.asp");
ReadableByteChannel rbc = Channels.newChannel(website.openStream());
FileOutputStream fos = new FileOutputStream("information.html");
fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
Using transferFrom() is potentially much more efficient than a simple loop that reads from the source channel and writes to this channel. Many operating systems can transfer bytes directly from the source channel into the filesystem cache without actually copying them.