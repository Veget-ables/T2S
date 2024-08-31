package com.tsuchinoko.t2s.feature.schedule.gen

import com.tsuchinoko.t2s.core.model.ScheduleEvent
import java.time.LocalDateTime
import java.util.UUID

internal val fakePrompt = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n 2020年2月17日　終日予定\n これはメモです\n "

internal val fakeRegularEvent = createScheduleEvent(
    title = "通常予定",
    memo = "これはメモです",
    start = LocalDateTime.parse("2020-02-15T01:30"),
    end = LocalDateTime.parse("2020-02-15T23:30"),
)

internal val fakeAllDayEvent = createScheduleEvent(
    title = "終日予定",
    memo = "これはメモです",
    start = LocalDateTime.parse("2020-02-15T00:00"),
    end = LocalDateTime.parse("2020-02-15T23:59"),
)

internal val fakeLongTitleEvent = createScheduleEvent(
    title = "タイトルがとても長くて2行以上になってしまう予定",
    start = LocalDateTime.parse("2020-02-15T21:30"),
    end = LocalDateTime.parse("2020-02-16T21:30"),
)

internal val fakeLongMemoEvent = createScheduleEvent(
    title = "メモが長い予定",
    memo = "6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。",
    start = LocalDateTime.parse("2020-02-15T21:30"),
    end = LocalDateTime.parse("2020-02-16T21:30"),
)

internal val fake2DaysEvent = createScheduleEvent(
    title = "日をまたぐ予定",
    start = LocalDateTime.parse("2020-02-15T21:30"),
    end = LocalDateTime.parse("2020-02-16T21:30"),
)

internal val fakeEvents = listOf(
    fakeRegularEvent,
    fakeAllDayEvent,
    fake2DaysEvent,
    fakeLongTitleEvent,
    fakeLongMemoEvent,
)

private fun createScheduleEvent(
    id: UUID = UUID.randomUUID(),
    title: String,
    memo: String? = null,
    start: LocalDateTime,
    end: LocalDateTime,
): ScheduleEvent = ScheduleEvent(id = id, title = title, memo = memo, start = start, end = end)
