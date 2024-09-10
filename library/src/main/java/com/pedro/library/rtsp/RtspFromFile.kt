/*
 * Copyright (C) 2024 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedro.library.rtsp

import android.content.Context
import android.media.MediaCodec
import android.os.Build
import androidx.annotation.RequiresApi
import com.pedro.common.AudioCodec
import com.pedro.common.ConnectChecker
import com.pedro.common.VideoCodec
import com.pedro.encoder.input.decoder.AudioDecoderInterface
import com.pedro.encoder.input.decoder.VideoDecoderInterface
import com.pedro.library.base.FromFileBase
import com.pedro.library.util.streamclient.RtspStreamClient
import com.pedro.library.util.streamclient.StreamClientListener
import com.pedro.library.view.OpenGlView
import com.pedro.rtsp.rtsp.RtspClient
import java.nio.ByteBuffer

/**
 * More documentation see:
 * [com.pedro.library.base.FromFileBase]
 *
 * Created by pedro on 4/06/17.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class RtspFromFile: FromFileBase {

  private val streamClientListener = object: StreamClientListener {
    override fun onRequestKeyframe() {
      requestKeyFrame()
    }
  }
  private lateinit var rtspClient: RtspClient
  private lateinit var streamClient: RtspStreamClient

  constructor(
    openGlView: OpenGlView, connectChecker: ConnectChecker,
    videoDecoderInterface: VideoDecoderInterface, audioDecoderInterface: AudioDecoderInterface
  ): super(openGlView, videoDecoderInterface, audioDecoderInterface) {
    init(connectChecker)
  }

  constructor(
    context: Context, connectChecker: ConnectChecker,
    videoDecoderInterface: VideoDecoderInterface, audioDecoderInterface: AudioDecoderInterface
  ): super(context, videoDecoderInterface, audioDecoderInterface) {
    init(connectChecker)
  }

  constructor(
    connectChecker: ConnectChecker,
    videoDecoderInterface: VideoDecoderInterface, audioDecoderInterface: AudioDecoderInterface
  ): super(videoDecoderInterface, audioDecoderInterface) {
    init(connectChecker)
  }

  private fun init(connectChecker: ConnectChecker) {
    rtspClient = RtspClient(connectChecker)
    streamClient = RtspStreamClient(rtspClient, streamClientListener)
  }

  override fun setVideoCodecImp(codec: VideoCodec) {
    rtspClient.setVideoCodec(codec)
  }

  override fun setAudioCodecImp(codec: AudioCodec) {
    rtspClient.setAudioCodec(codec)
  }

  override fun getStreamClient(): RtspStreamClient = streamClient

  override fun onAudioInfoImp(isStereo: Boolean, sampleRate: Int) {
    rtspClient.setAudioInfo(sampleRate, isStereo)
  }

  override fun startStreamImp(url: String) {
    rtspClient.connect(url)
  }

  override fun stopStreamImp() {
    rtspClient.disconnect()
  }

  override fun onVideoInfoImp(sps: ByteBuffer, pps: ByteBuffer?, vps: ByteBuffer?) {
    rtspClient.setVideoInfo(sps, pps, vps)
  }

  override fun getVideoDataImp(videoBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    rtspClient.sendVideo(videoBuffer, info)
  }

  override fun getAudioDataImp(audioBuffer: ByteBuffer, info: MediaCodec.BufferInfo) {
    rtspClient.sendAudio(audioBuffer, info)
  }
}
