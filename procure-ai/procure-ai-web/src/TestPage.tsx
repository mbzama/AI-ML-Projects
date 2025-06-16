import React from 'react';
import { Container, Title, Text, Button, Stack } from '@mantine/core';

export const TestPage: React.FC = () => {
  return (
    <Container size="md" py="xl">
      <Stack gap="md">
        <Title order={1}>Mantine Test Page</Title>
        <Text>This is a test page to verify Mantine is working correctly.</Text>
        <Button onClick={() => alert('Button works!')}>
          Test Button
        </Button>
      </Stack>
    </Container>
  );
};
