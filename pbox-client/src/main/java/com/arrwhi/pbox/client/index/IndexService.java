//package com.arrwhi.pbox.client.index;
//
///**
// * Created by arran on 30/07/17.
// *
// * Singleton Service for interacting with the Index.
// * i.e. loading, updating, etc.
// *
// * TODO: Not really used yet.
// */
//public enum IndexService {
//
//    INSTANCE;
//
//    private IndexIO indexio;
//    private Index index;
//
//    IndexService() {
//        this.indexio = new IndexIO();
//    }
//
//    public void load() {
//        if(indexio.indexExists()) {
//            loadNotFirstTime();
//        } else {
//            loadFirstTime();
//        }
//    }
//
//    private void loadFirstTime() {
//        this.index = FileSystemIndexer.buildIndex();
//        writeToIndex();
//    }
//
//    private void loadNotFirstTime() {
//        Index newIndex = FileSystemIndexer.buildIndex();
//        Index oldIndex = indexio.read();
//
//        IndexComparator indexComparator = new IndexComparator(oldIndex, newIndex);
//        if (!indexComparator.areEqual()) {
//            System.out.println("Num differences since last time: " + indexComparator.getDifferences().size());
//
//            // TODO: handle differences.
//            // write differences to index.
//            // write differences to network.
//            // write index to disk (indexio.write(newIndex);)
//        } else {
//            System.out.println("No differences since last time.");
//        }
//
//        this.index = newIndex;
//    }
//
//    private void writeToIndex() {
//        indexio.write(index);
//    }
//
//    public Index index() {
//        return index;
//    }
//
//    public void confirmTransportFileDelivery(String hash) {
////        try {
////            IndexEntry ie = index.getByHash(hash);
////            ie.setSynced(true);
////            indexio.write(index);
////        } catch (IndexEntryNotFoundException e) {
////            e.printStackTrace();
////        }
//    }
//}
