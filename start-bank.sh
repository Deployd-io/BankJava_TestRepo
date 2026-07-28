#!/usr/bin/env bash
cd /home/victor/logrite/projects/BankJava_TestRepo || exit 1
exec java -jar target/bankingportal-0.0.1-SNAPSHOT.jar >> /home/victor/logrite/projects/bank_agent_rs/bank.log 2>&1
