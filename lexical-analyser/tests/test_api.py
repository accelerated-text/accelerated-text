import pytest


def test_healthcheck(client):
    assert client.get('/health').status_code == 200
