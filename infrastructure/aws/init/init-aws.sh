#!/bin/bash

# Initialize S3 Bucket
awslocal s3api create-bucket --bucket $S3_BUCKET_NAME --create-bucket-configuration LocationConstraint=$AWS_DEFAULT_REGION
awslocal s3api put-object --bucket $S3_BUCKET_NAME --key $S3_BUCKET_ROOT_DIR
awslocal s3api put-object --bucket $S3_BUCKET_NAME --key $S3_BUCKET_MEMBER_DIR
awslocal s3api put-object --bucket $S3_BUCKET_NAME --key $S3_BUCKET_CREW_DIR
awslocal s3api put-object --bucket $S3_BUCKET_NAME --key $S3_BUCKET_SQUAD_DIR
awslocal s3 ls
awslocal s3 ls s3://$S3_BUCKET_NAME --recursive
