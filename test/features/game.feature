Feature: Berlin Clock

  Scenario: Berlin clock for midnight
    Given the API endpoint /time
    When I request the time for 00:00:00
    Then the seconds are Y
    And the 5 hours is OOOO
    And the 1 hour is OOOO
    And the 5 minute is OOOOOOOOOOO
    And the 1 minute is OOOO

