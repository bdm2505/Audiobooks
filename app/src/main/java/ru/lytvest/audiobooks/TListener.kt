package ru.lytvest.audiobooks

import com.frostwire.jlibtorrent.TorrentHandle
import com.masterwok.simpletorrentandroid.contracts.TorrentSessionListener
import com.masterwok.simpletorrentandroid.models.TorrentSessionStatus
import kotlinx.coroutines.*

class TListener(private val scope: CoroutineScope, val updater: (TorrentHandle) -> Unit) : TorrentSessionListener {


    override fun onAddTorrent(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {

        scope.launch(Dispatchers.Main) {
            updater(torrentHandle)
        }
    }

    override fun onBlockUploaded(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onMetadataFailed(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onMetadataReceived(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onPieceFinished(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {

        scope.launch(Dispatchers.Main) {
            updater(torrentHandle)
        }
    }

    override fun onTorrentDeleteFailed(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onTorrentDeleted(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onTorrentError(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onTorrentFinished(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
        updater(torrentHandle)
    }

    override fun onTorrentPaused(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onTorrentRemoved(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }

    override fun onTorrentResumed(
        torrentHandle: TorrentHandle,
        torrentSessionStatus: TorrentSessionStatus
    ) {
    }
}