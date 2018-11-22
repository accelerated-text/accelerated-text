PREACT_MAKE= cd preact-demo && make

.PHONY: test
test:
	${PREACT_MAKE} test

.PHONY: run
run:
	${PREACT_MAKE} run

.PHONY: clean
clean:
	${PREACT_MAKE} clean

