package com.gummybearstudio.quicksudoku.ui.menu

interface IMenuObserver {
    fun onNewGame()
    fun onResumeGame()
    fun onExit()
}