/**
 * Logs a message to the console in green.
 * @param msg The message you want to log.
 */
void debug(Object msg){
  if(levelCheck(['DEBUG'])){
    ansiColor('xterm'){
      println("\u001b[32m[Debug] ${msg.toString()}\u001b[0m")
    }
  }
}

/**
 * Logs a message to the console in green.
 * @param msg The message you want to log.
 */
void info(Object msg){
  if(levelCheck(['DEBUG', 'INFO'])){
    ansiColor('xterm'){
        println("\u001B[34m[Info] ${msg.toString()}\u001B[0m")
    }
  }
}

/**
 * Logs a message to the console in yellow.
 * @param msg The message you want to log.
 */
void warn(Object msg){
  if(levelCheck(['DEBUG', 'INFO', 'WARN'])){
    ansiColor('xterm'){
        println("\u001B[33m[Warning] ${msg.toString()}\u001B[0m")
    }
  }
}

/**
 * Logs a message to the console in red.
 * @param msg The message you want to log.
 */
void error(Object msg){
  if(levelCheck(['DEBUG', 'INFO', 'WARN', 'ERROR'])){
    ansiColor('xterm'){
        println("\u001B[31m[Error] ${msg.toString()}\u001B[0m")
    }
  }
}

/**
 * Check if the current level should be logged.
 * @param levels The levels that you want to log to.
 * @return <code>true</code> If the current level is in
 *         levels param and <code>false</code> if not.
 */
Boolean levelCheck(List levels){
    String level = env.PIPELINE_LOG_LEVEL ?: 'INFO'
    return levels.contains(level)
}
